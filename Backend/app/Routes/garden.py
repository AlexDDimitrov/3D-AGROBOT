import logging
from flask import Blueprint, request, jsonify
from ..middleware.auth_guard import login_required
from ..models.garden import GardenModel

garden_bp = Blueprint("garden", __name__)

@garden_bp.get("/list")
@login_required
def list_gardens():
    user_id = request.user_id  # взето от токена

    gardens = GardenModel.get_by_user_id(user_id)

    logging.info(f"List gardens - user_id: {user_id}")
    return jsonify({
        "result": 0,
        "gardens": gardens
    }), 200

