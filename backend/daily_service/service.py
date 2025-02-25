import os
from datetime import datetime, timedelta
from typing import List

from endpoints.teams.model import EventData
from services.get_env import LAST_TIME
from services.mongo import USERS_PREDICTS


def get_timestamps():
    if os.path.exists(LAST_TIME):
        with open(LAST_TIME, 'r') as file:
            last_time_str = file.read().strip()
        last_date = datetime.fromisoformat(last_time_str)
    else:
        last_date = datetime.now() - timedelta(days=1)
        with open(LAST_TIME, 'w') as file:
            file.write(last_date.isoformat())

    start_date = last_date.replace(hour=0, minute=0, second=0, microsecond=0)

    current_date = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)

    timestamps = []
    temp_date = start_date

    while temp_date < current_date:
        timestamps.append(int(temp_date.timestamp()))
        temp_date += timedelta(days=1)

    return timestamps


def update_last_time(new_time):
    with open(LAST_TIME, 'w') as file:
        file.write(new_time.isoformat())


def check_prediction(prediction, actual):
    pred_home, pred_away = map(int, prediction.split(':'))
    actual_home, actual_away = map(int, actual.split(':'))

    pred_winner = 1 if pred_home > pred_away else 0
    actual_winner = 1 if actual_home > actual_away else 0

    winner_guessed = 1 if pred_winner == actual_winner else 0

    if prediction == actual:
        score_accuracy = 3
    elif abs(pred_home - actual_home) + abs(pred_away - actual_away) == 1:
        score_accuracy = 2
    elif abs(pred_home - actual_home) + abs(pred_away - actual_away) == 2:
        score_accuracy = 1
    else:
        score_accuracy = 0

    return winner_guessed, score_accuracy


def group_events_by_date(events: List[EventData]):
    events_dict = {}
    for event in events:
        day = str(event.start_at_day)
        if day not in events_dict:
            events_dict[day] = []

        event_info = {
            "event": event.id,
            "score": event.score
        }

        events_dict[day].append(event_info)
    return events_dict
