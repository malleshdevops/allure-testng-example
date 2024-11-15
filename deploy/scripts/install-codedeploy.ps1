# Path to the CodeDeploy agent installer
$agentInstaller = "C:/app/codedeploy-agent/codedeploy-agent.msi"

# Check if the agent is already installed
$codedeployService = Get-Service -Name "codedeploy-agent" -ErrorAction SilentlyContinue
if ($null -eq $codedeployService) {
    Write-Host "Installing AWS CodeDeploy agent..."
    Start-Process -FilePath "msiexec.exe" -ArgumentList "/i $agentInstaller /quiet" -Wait
    Write-Host "CodeDeploy agent installed successfully."
} else {
    Write-Host "CodeDeploy agent is already installed."
}

# Ensure the service is running
Start-Service -Name "codedeploy-agent"
Write-Host "CodeDeploy agent is running."

