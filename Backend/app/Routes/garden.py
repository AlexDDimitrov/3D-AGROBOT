import logging
from flask import Blueprint, request, jsonify
from ..middleware.auth_guard import login_required
from ..models.garden import GardenModel

garden_bp = Blueprint("garden", __name__)

@garden_bp.get("/list")
@login_required
def list_gardens():
    user_id = request.user_id
    gardens = GardenModel.get_by_user_id(user_id)
    logging.info(f"List gardens - user_id: {user_id}")
    return jsonify({"result": 0, "gardens": gardens}), 200

@garden_bp.post("/create")
@login_required
def create_garden():
    data = request.get_json()
    user_id = request.user_id

    garden_name = data.get("garden_name")
    garden_width = data.get("garden_width")
    garden_height = data.get("garden_height")
    path_width = data.get("path_width")
    number_beds = data.get("number_beds")
    plant = data.get("plant")

    if not all([garden_name, garden_width, garden_height, path_width, number_beds, plant]):
        return jsonify({"result": 202}), 400

    GardenModel.create(user_id, garden_name, garden_width, garden_height, path_width, number_beds, plant)

    logging.info(f"Create garden - user_id: {user_id} - {garden_name}")
    return jsonify({"result": 0}), 201