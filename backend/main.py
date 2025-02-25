import threading
import time
from datetime import timedelta
import schedule
from flask import Flask
from flask_jwt_extended import JWTManager

from daily_service.daily_service import daily_service
from endpoints.auth.auth import auth_bp
from endpoints.followers.followers import followers_bp
from endpoints.predict.predict import predict_bp
from endpoints.stats.stats import stats_bp
from endpoints.teams.teams import teams_bp
from services.get_env import PORT, JWT_KEY

app = Flask(__name__)
app.config['JWT_SECRET_KEY'] = JWT_KEY
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=30)
app.config['JWT_REFRESH_TOKEN_EXPIRES'] = timedelta(days=30)
app.config["JWT_HEADER_NAME"] = "Authorization"
app.config["JWT_HEADER_TYPE"] = "Bearer"
app.config["JWT_COOKIE_CSRF_PROTECT"] = False

jwt_manager = JWTManager(app)

app.register_blueprint(auth_bp, url_prefix='/auth')
app.register_blueprint(teams_bp, url_prefix='/teams')
app.register_blueprint(predict_bp, url_prefix='/predict')
app.register_blueprint(followers_bp, url_prefix='/followers')
app.register_blueprint(stats_bp, url_prefix='/stats')


@app.route('/is_ok')
def home():
    return "Server is running"


def run_scheduler():
    while True:
        schedule.run_pending()
        time.sleep(60)


if __name__ == '__main__':
    schedule.every().day.at("00:53").do(daily_service)
    scheduler_thread = threading.Thread(target=run_scheduler)
    scheduler_thread.daemon = True
    scheduler_thread.start()

    app.run(host='0.0.0.0', port=PORT, debug=False)
