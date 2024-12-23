from flask import Flask, request, jsonify
from log_util import log, init_logger
import json
import detect
import os

app = Flask(__name__)
tm = None

def init_track_machine() -> detect.TrackMachine:
    script_dir = os.path.dirname(os.path.abspath(__file__))
    config_path = os.path.join(script_dir, 'config.json')
    with open(config_path, 'r') as f:
        config = json.load(f)
    w = config['width']
    h = config['height']
    cam_id = config['cam_id']
    tm = detect.TrackMachine(cam_id, w, h)
    log(f"Track machine initialized, cam_id: {cam_id}, width: {w}, height: {h}")
    return tm


@app.route('/img', methods=['POST'])
def upload_image():
    log("Image upload request received")
    global tm
    success = True
    count = 0
    try:
        cam_id = request.args.get('cam_id')
        image_file = request.files.get('file')
        frame_byte_array = image_file.read()

        count = tm.image_input(frame_byte_array, cam_id)
        log(f"Image uploaded, {count} changes detected")

    except Exception as e:
        success = False
        log(f"Error: {e}")
    return jsonify({"change": count, "OK": success})


if __name__ == '__main__':    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    log_path = os.path.join(script_dir, 'detection.log')
    init_logger(output_file=log_path)
    tm = init_track_machine()
    app.run(debug=False, host="0.0.0.0", port=5987)

