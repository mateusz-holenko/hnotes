import jwt

def decode_jwt(token):
    return jwt.PyJWT().decode(token, options={'verify_signature': False})

