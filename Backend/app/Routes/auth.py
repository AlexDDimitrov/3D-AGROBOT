from flask import Blueprint, request, jsonify
from ..services.auth_service import register_user

auth_bp = Blueprint("auth", __name__)

@auth_bp.post("/register")
def register():
    data = request.get_json()

    first_name = data.get("first_name")
    last_name = data.get("last_name")
    email = data.get("email")
    password = data.get("password")

    if not all([first_name, last_name, email, password]):
        return jsonify({"error": "Всички полета са задължителни"}), 400

    success, message = register_user(first_name, last_name, email, password)

    if not success:
        return jsonify({"error": message}), 409

    return jsonify({"message": message}), 201