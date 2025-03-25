from flask import Flask, request

app = Flask(__name__)

@app.route('/')
def health_check():
    return { 'status': 'ok' }


@app.route('/verificator', methods=['POST'])
def verify_input():
    input = str(request.data)
    accepted = 'banana' not in input
    result = {
        'status': 'accepted' if accepted else 'rejected',
        'length': len(request.data)
    }
    app.logger.info('Input is: ' + input + "; result is: " + str(result))
    return result
