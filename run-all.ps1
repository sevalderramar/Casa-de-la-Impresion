if ([string]::IsNullOrWhiteSpace($env:JWT_SECRET)) {
    Write-Error "JWT_SECRET no esta configurado. Define la variable de entorno antes de ejecutar este script."
    exit 1
}

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd pedido-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd cliente-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd producto-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd despacho-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd fabricacion-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd estado-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd metrica-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd transportista-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd log-service; .\mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd auth-service; .\mvnw spring-boot:run"
