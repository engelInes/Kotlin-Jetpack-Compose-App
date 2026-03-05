import requests
import os

token = os.environ["GITHUB_TOKEN"]
owner = os.environ["OWNER"]
repo = os.environ["REPO"]
pr = os.environ["PR_NUMBER"]

headers = {
    "Authorization": f"Bearer {token}",
    "Accept": "application/vnd.github+json"
}

files_url = f"https://api.github.com/repos/{owner}/{repo}/pulls/{pr}/files"
files = requests.get(files_url, headers=headers).json()

labels = set()

for f in files:
    name = f["filename"]

    if "/core/" in name:
        labels.add("core")

    if "/infra/" in name:
        labels.add("infra")

    if "/ui/" in name:
        labels.add("ui")

    if "test/" in name:
        labels.add("test")

    if ".github/" in name:
        labels.add("ci")

if labels:
    requests.post(
        f"https://api.github.com/repos/{owner}/{repo}/issues/{pr}/labels",
        headers=headers,
        json={"labels": list(labels)}
    )