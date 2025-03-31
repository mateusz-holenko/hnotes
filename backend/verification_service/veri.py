from flask import Flask, request, Response
from werkzeug.exceptions import InternalServerError


app = Flask(__name__)
ctr = 0


def maybe_error():
    global ctr
    ctr += 1
    if ctr % 5 == 0:
        raise InternalServerError('Encountered an internal error, pls try again later')


@app.route('/')
def health_check():
    return { 'status': 'ok' }


@app.route('/verificator', methods=['POST'])
def verify_input():
    maybe_error()

    input = str(request.data)
    accepted = 'banana' not in input
    result = {
        'status': 'accepted' if accepted else 'rejected',
        'length': len(request.data)
    }
    app.logger.info('Input is: ' + input + "; result is: " + str(result))
    return result


@app.route('/error')
def get_error():
    return Response('there seems to be an error', 500)
