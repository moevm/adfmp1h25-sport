from datetime import datetime

from daily_service.service import get_timestamps, group_events_by_date, check_prediction
from endpoints.stats.services import update_stats
from endpoints.teams.service import get_events_service
from services.mongo import USERS_PREDICTS_FOR_DAILY_SERVICE


def daily_service():
    timestamps = get_timestamps()
    events = get_events_service(start_time=timestamps[0], end_time=int(datetime.now().timestamp()))
    events_by_date = group_events_by_date(events)
    predicts = list(USERS_PREDICTS_FOR_DAILY_SERVICE.find({"day": {"$in": timestamps}}))

    for predict in predicts:
        day = str(predict['day'])
        if day not in events_by_date:
            continue

        for event_id, user_predictions in predict['events'].items():
            event_id = str(event_id)
            matched_event = None
            for event_data in events_by_date.get(str(day), []):
                if str(event_data['event']) == event_id:
                    matched_event = event_data
                    break

            if not matched_event:
                continue

            actual_score = matched_event['score']

            for user_id, predicted_score in user_predictions.items():
                winner_guessed, score_accuracy = check_prediction(predicted_score, actual_score)

                update_stats(
                    user_id,
                    inc_predicted_games=1,
                    inc_winner_points=winner_guessed,
                    inc_score_points=score_accuracy
                )
