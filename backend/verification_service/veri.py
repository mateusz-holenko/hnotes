from flask import Flask, request

app = Flask(__name__)

@app.route('/')
def health_check():
    return { 'status': 'ok' }


@app.route('/verificator', methods=['POST'])
def verify_input():
    return { 'length': len(request.data) }
