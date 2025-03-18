from urllib.parse import urlencode
import requests
from endpoints.teams.model import EventData
from services.get_env import KHL_URL


def get_events_service(start_time, end_time, teams=None):
    all_events = []
    page = 1

    while True:
        params = [
            ('page', page),
            ('per_page', 16)
        ]

        if start_time:
            params.append(('q[start_at_gt_time_from_unixtime]', start_time))
        if end_time:
            params.append(('q[start_at_lt_time_from_unixtime]', end_time))
        if teams:
            # Предполагаем, что teams уже приходит как массив int
            for team in teams:
                params.append(('q[team_a_or_team_b_in][]', team))

        try:
            url = f"{KHL_URL}/events_v2.json"
            query_string = urlencode(params, safe='[]', doseq=True)
            url = f"{url}?{query_string}"

            response = requests.get(url)
            response.raise_for_status()
            data = response.json()

            current_page_events = [EventData.from_json(item) for item in data]
            all_events.extend(current_page_events)

            if len(current_page_events) < 16 or page > 9:
                break

            page += 1

        except requests.exceptions.RequestException as e:
            return None

    return all_events
