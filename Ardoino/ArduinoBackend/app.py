import serial
import time
import requests

# ==========================================
# КОНФИГУРАЦИЯ
# ==========================================
# 1. Настройки за главния бекенд (Flask)
# Смени с реалния IP адрес на компютъра/сървъра, където върви Flask бекендът
API_URL = "http://192.168.1.100:5000/garden/route/" 
GARDEN_ID = 1 # ID на градината, която ще се обхожда
AUTH_TOKEN = "твой_токен_тук" # Токен за автентикация (@login_required)

# 2. Настройки за връзка с ESP32
# Провери на кой порт е вързано ESP-то (обикновено /dev/ttyUSB0 или /dev/ttyACM0)
ESP_PORT = '/dev/ttyUSB0' 
BAUD_RATE = 115200
# ==========================================

def connect_to_esp():
    """Установява серийна връзка с ESP32."""
    try:
        esp = serial.Serial(ESP_PORT, BAUD_RATE, timeout=1)
        time.sleep(2) # Чакаме ESP да се рестартира и инициализира
        print("[Pi] Връзката с ESP32 е успешна.")
        return esp
    except Exception as e:
        print(f"[Pi] ГРЕШКА при свързване с ESP32 на порт {ESP_PORT}: {e}")
        exit(1)

def fetch_route_from_backend():
    """Изтегля изчисления маршрут от Flask API."""
    print(f"[Pi] Изтегляне на маршрут за градина ID: {GARDEN_ID}...")
    headers = {
        "Authorization": f"Bearer {AUTH_TOKEN}",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(f"{API_URL}{GARDEN_ID}", headers=headers)
        if response.status_code == 200:
            data = response.json()
            if data.get("result") == 0:
                print(f"[Pi] Маршрутът за '{data.get('garden_name', 'Неизвестна')}' е изтеглен успешно!")
                return data.get("commands", [])
        print(f"[Pi] Грешка от сървъра: HTTP {response.status_code} - {response.text}")
        return []
    except Exception as e:
        print(f"[Pi] Мрежова грешка при връзка с бекенда: {e}")
        return []

def execute_mission():
    """Основен цикъл, който подава командите към ESP32 и чака отговор."""
    commands = fetch_route_from_backend()
    
    if not commands:
        print("[Pi] Няма команди за изпълнение. Прекратяване на мисията.")
        return

    esp_serial = connect_to_esp()
    
    print("\n=== СТАРТ НА МИСИЯТА ===")
    for step_num, cmd in enumerate(commands, 1):
        print(f"\n[Стъпка {step_num}/{len(commands)}] Изпращам към ESP32: {cmd}")
        
        # Пращаме командата по серийния порт
        esp_serial.write((cmd + '\n').encode('utf-8'))
        
        # Блокираме и чакаме ESP32 да свърши работата и да върне "DONE"
        while True:
            if esp_serial.in_waiting > 0:
                esp_response = esp_serial.readline().decode('utf-8').strip()
                
                if esp_response == "DONE":
                    print(f" -> Стъпка {step_num} изпълнена успешно!")
                    time.sleep(0.5) # Кратка пауза преди следващата команда
                    break
                elif esp_response:
                    # Показваме съобщения от ESP32 (дебъг)
                    print(f" -> [ESP32 Log]: {esp_response}")
                    
            time.sleep(0.05) # Предотвратява натоварване на процесора на Pi
            
    print("\n=== МИСИЯТА Е ЗАВЪРШЕНА УСПЕШНО! РОБОТЪТ Е В БАЗАТА. ===")

if __name__ == "__main__":
    try:
        execute_mission()
    except KeyboardInterrupt:
        print("\n[Pi] Мисията е прекъсната принудително от потребителя (Ctrl+C).")