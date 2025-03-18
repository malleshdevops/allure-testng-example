import requests
import os

GITHUB_TOKEN = os.getenv("GH_TOKEN")
REPO = "your-org/your-repo"
HEAD_BRANCH = os.getenv("BRANCH")

# Get the PR ID
headers = {"Authorization": f"token {GITHUB_TOKEN}"}
response = requests.get(f"https://api.github.com/repos/{REPO}/pulls", headers=headers)
pr_list = response.json()

for pr in pr_list:
    if pr['head']['ref'] == HEAD_BRANCH:
        pr_id = pr['number']

        # Merge PR if it passes checks
        merge_url = f"https://api.github.com/repos/{REPO}/pulls/{pr_id}/merge"
        merge_response = requests.put(merge_url, headers=headers)
        
        if merge_response.status_code == 200:
            print(f"PR #{pr_id} merged successfully!")
        else:
            print(f"Failed to merge PR #{pr_id}")
