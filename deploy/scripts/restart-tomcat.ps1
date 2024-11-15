# Detect the Tomcat folder
$tomcatFolder = Get-ChildItem -Path "C:/app/" -Directory | Where-Object { $_.Name -match "apache-tomcat" } | Select-Object -ExpandProperty FullName

if ($null -ne $tomcatFolder) {
    Write-Host "Restarting Tomcat located at $tomcatFolder"
    Start-Process -FilePath "$tomcatFolder/bin/shutdown.bat" -NoNewWindow -Wait
    Start-Process -FilePath "$tomcatFolder/bin/startup.bat" -NoNewWindow -Wait
} else {
    Write-Host "Tomcat folder not found in C:/app/"
    Exit 1
}

