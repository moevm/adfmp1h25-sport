from flask import Blueprint, request, jsonify
from flask_jwt_extended import create_access_token, create_refresh_token, jwt_required, get_jwt_identity

from endpoints.auth.datasource import register_user, find_user
from endpoints.auth.model import AuthData

auth = Blueprint('auth', __name__)


def create_tokens(identity: str) -> dict:
    access_token = create_access_token(identity=identity)
    refresh_token = create_refresh_token(identity=identity)
    return {
        'access_token': access_token,
        'refresh_token': refresh_token
    }


def create_identity(user_id: str) -> str:
    return str({'id': user_id})


@auth.route('/login', methods=['POST'])
def login():
    if not request.is_json:
        return jsonify({'message': 'Content-Type must be application/json'}), 415
    data = request.get_json()
    if not data:
        return jsonify({'message': 'Missing request body'}), 400
    try:
        auth_data = AuthData.from_json(data)
    except ValueError as e:
        return jsonify({'message': str(e)}), 400
    if not auth_data.is_valid():
        return jsonify({'message': 'Wrong login or password'}), 400
    user_id = find_user(auth_data)
    if user_id:
        return jsonify(create_tokens(create_identity(user_id))), 201

    return jsonify({'message': 'User with this login already exists'}), 409


@auth.route('/refresh', methods=['POST'])
@jwt_required(refresh=True)
def refresh():
    identity = get_jwt_identity()
    print(identity)
    return jsonify(create_tokens(identity)), 200


@auth.route('/register', methods=['POST'])
def register():
    if not request.is_json:
        return jsonify({'message': 'Content-Type must be application/json'}), 415
    data = request.get_json()
    if not data:
        return jsonify({'message': 'Missing request body'}), 400
    try:
        auth_data = AuthData.from_json(data)
    except ValueError as e:
        return jsonify({'message': str(e)}), 400
    if not auth_data.is_valid():
        return jsonify({'message': 'Missing required fields'}), 400
    user_id = register_user(auth_data)
    if user_id:
        return jsonify(create_tokens(create_identity(user_id))), 201

    return jsonify({'message': 'User with this login already exists'}), 409


