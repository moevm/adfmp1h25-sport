import requests as requests
from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required
import logging

from endpoints.teams.service import get_events_service
from services.get_env import KHL_URL

teams_bp = Blueprint('teams', __name__)


@teams_bp.route('/get_teams', methods=['GET'])
@jwt_required()
def get_teams():
    response = requests.get(f'{KHL_URL}/teams_v2.json')
    return jsonify(response.json()), response.status_code


@teams_bp.route('/get_events', methods=['GET'])
@jwt_required()
def get_events():
    start_time = request.args.get('start_time')
    end_time = request.args.get('end_time')
    teams = request.args.getlist('teams')
    teams = [int(team) for team in teams]


    events = get_events_service(start_time, end_time, teams)
    if events is not None:
        return jsonify([event.to_json() for event in events]), 200
    else:
        return jsonify({'message': "cant find events"}), 500

