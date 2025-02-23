from urllib.parse import urlencode

import requests as requests
from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required

from endpoints.teams.model import EventData
from services.get_env import KHL_URL

teams = Blueprint('teams', __name__)


@teams.route('/get_teams', methods=['GET'])
@jwt_required()
def get_teams():
    response = requests.get(f'{KHL_URL}/teams_v2.json')
    return jsonify(response.json()), response.status_code


@teams.route('/get_events', methods=['GET'])
@jwt_required()
def get_events():
    start_time = request.args.get('start_time')
    end_time = request.args.get('end_time')
    teams = request.args.getlist('teams')
    page = request.args.get('page')
    params = []

    if start_time:
        params.append(('q[start_at_gt_time_from_unixtime]', start_time))
    if end_time:
        params.append(('q[start_at_lt_time_from_unixtime]', end_time))
    if teams:
        teams = [int(x.strip()) for x in teams[0].split(',')]
        for team in teams:
            params.append(('q[team_a_or_team_b_in][]', team))
    if page:
        params.append(('page', page))

    try:
        url = f"{KHL_URL}/events_v2.json"
        if params:
            query_string = urlencode(params, safe='[]', doseq=True)
            url = f"{url}?{query_string}"
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        events = [EventData.from_json(item) for item in data]
        return jsonify([event.to_json() for event in events]), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({'error': str(e)}), 500