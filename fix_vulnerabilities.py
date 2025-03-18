import os
import json
import requests

# Load vulnerability reports
with open('snyk-report.json', 'r') as f:
    snyk_data = json.load(f)

fixes_made = False

# Auto-fix dependencies
for vuln in snyk_data.get('vulnerabilities', []):
    package = vuln.get('package')
    fix = vuln.get('fix')
    
    if fix:
        print(f"Applying fix: {fix} for {package}")
        os.system(f"npm install {package}@latest" if package else "")

        fixes_made = True

# If no fixes were made, call GitHub Copilot API for suggestions
if not fixes_made:
    print("No direct fixes available. Using GitHub Copilot...")
    copilot_suggestion = "TODO: Call GitHub Copilot API here"
    with open("fix_suggestions.txt", "w") as f:
        f.write(copilot_suggestion)
