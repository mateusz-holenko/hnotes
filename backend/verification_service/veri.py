import os
import json
import logging
from logging.config import dictConfig
import stomp

from flask import Flask, request, Response, session
from werkzeug.exceptions import InternalServerError


dictConfig({
    'version': 1,
    'root': {
        'level': 'DEBUG'
    }
})


class VerificationQueueListener(stomp.ConnectionListener):
    def __init__(self, connection):
        self.connection = connection


    def on_error(self, frame):
        app.logger.error('received an err: %s', frame)


    def on_message(self, frame):
        app.logger.debug('received a verification request for: %s', frame.body)

        result = do_verification(frame.body)

        self.connection.send(body=json.dumps(result), destination='verification-result.queue')
        app.logger.debug('done')


def connect_to_artemis():
    artemis_host = os.environ.get('HNOTES_ARTEMIS_HOST', 'localhost')
    artemis_port = os.environ.get('HNTOES_ARTEMIS_PORT', 61616)
    c = stomp.Connection([(artemis_host, artemis_port)], heartbeats=(4000, 4000))
    c.set_listener('', VerificationQueueListener(c))

    c.connect('artemis', 'artemis', wait=True)
    c.subscribe(destination='verification.queue', id=1)

    return c


def do_verification(text: str) -> bool:
    blocked_words = [
        'banana'
    ]

    accepted = True
    for b in blocked_words:
        if b in text:
            accepted = False
            break

    return {
        'status': 'accepted' if accepted else 'rejected',
        'length': len(text)
    }


def maybe_error():
    ctr = session.get('counter', 0)
    ctr += 1
    session['counter'] = ctr
    if ctr % 5 != 0:
        raise InternalServerError('Encountered an internal error, pls try again later')


try:
    connect_to_artemis()
except stomp.exception.ConnectFailedException:
    logger = logging.getLogger(__name__)
    logger.error("Couldn't connect to Artemis")

app = Flask(__name__)


@app.route('/')
def health_check():
    return { 'status': 'ok' }


@app.route('/verificator', methods=['POST'])
def verify_input():
    maybe_error()

    txt = str(request.data)
    result = do_verification(txt)

    app.logger.info('Input is: %s; result is: %s', input, str(result))

    return result


@app.route('/error')
def get_error():
    return Response('there seems to be an error', 500)
