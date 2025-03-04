from bson import ObjectId
from flask import Blueprint, jsonify

from endpoints.stats.services import LEVELS, get_users_stats
from services.decorators import get_user_id, token_required

stats_bp = Blueprint('stats', __name__)


@stats_bp.route('/get_levels', methods=['GET'])
@token_required
def get_levels():
    return jsonify(LEVELS)


@stats_bp.route('/get_stats', methods=['GET'])
@get_user_id
def get_stats(user_id):
    res = get_users_stats([ObjectId(user_id)])
    return res
