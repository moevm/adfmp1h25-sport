from datetime import datetime, timedelta
from urllib.parse import urlencode

import requests

from endpoints.teams.model import EventData
from services.get_env import KHL_URL


def handler(day: datetime):
    end_of_day = day.replace(hour=0, minute=0, second=0, microsecond=0)
    start_of_day = end_of_day - timedelta(days=1)

    start_time = int(start_of_day.timestamp() * 1000)
    end_time = int(end_of_day.timestamp() * 1000)


    params = [
        ('q[start_at_gt_time_from_unixtime]', start_time),
        ('q[start_at_lt_time_from_unixtime]', end_time)
    ]

    url = f"{KHL_URL}/events_v2.json"
    if params:
        query_string = urlencode(params, safe='[]', doseq=True)
        url = f"{url}?{query_string}"

    events = [EventData.from_json(item) for item in requests.get(url).json()]

