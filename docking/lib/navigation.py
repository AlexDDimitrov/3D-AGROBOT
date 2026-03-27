import time
import logging

from .esp32 import ESP32Connection
from .camera import CameraServer
from .gemini import analyze_image

log = logging.getLogger(__name__)


def generate_commands(garden: dict) -> list[str]:
    garden_width = garden["garden_width"]
    path_width = garden["path_width"]
    number_beds = garden["number_beds"]
    
    commands = []
    total_path_width_horizontal = (number_beds + 1) * path_width
    available_width_for_beds = garden_width - total_path_width_horizontal
    
    if number_beds <= 0 or available_width_for_beds <= 0:
        log.warning("Невалидни размери на градината или твърде много лехи!")
        return []

    bed_width = available_width_for_beds / number_beds
    distance_to_first_bed_center = path_width + (bed_width / 2)
    
    current_x = 0
    for i in range(number_beds):
        if i == 0:
            move_dist = distance_to_first_bed_center
        else:
            move_dist = bed_width + path_width
            
        commands.append(f"M {int(move_dist)}") # Движи се напред
        commands.append("C")                   # Снимай
        current_x += move_dist

    commands.append("T 180")
    commands.append(f"M {int(current_x)}")
    commands.append("T 180")
    
    return commands


def execute_mission(esp: ESP32Connection, camera: CameraServer, garden: dict) -> list[dict]:
    commands = generate_commands(garden)
    plant = garden["plant"]
    results = []

    log.info(f"Мисия: {len(commands)} команди за {garden['number_beds']} лехи")

    for cmd in commands:
        log.info(f"-> Текуща команда: {cmd}")
        
        # Ако командата е за снимане, Raspberry Pi я поема
        if cmd == "C":
            log.info("📸 Активиране на камерата...")
            image_path = camera.capture()
            
            if image_path:
                log.info(f"🤖 Изпращане на {image_path} към Gemini за анализ на {plant}...")
                analysis = analyze_image(image_path, plant)
                results.append({"image": image_path, "analysis": analysis})
                
                # Показваме само първите 150 символа от отговора на Gemini в лога
                safe_analysis = str(analysis)[:150].replace('\n', ' ')
                log.info(f"Резултат: {safe_analysis}...")
        
        # Ако командата е за движение (M) или завъртане (T), я пращаме на ESP32
        else:
            resp = esp.send(cmd)
            log.info(f"<- ESP32 отговори: {resp}")
            
        # Кратка пауза между действията, за да не се претоварва системата
        time.sleep(1)

    return results