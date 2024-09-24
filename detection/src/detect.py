from ultralytics import YOLO
import cv2
import numpy as np
from PIL import Image
import io
from deep_sort_realtime.deepsort_tracker import DeepSort
from typing import *


def byte_array_to_frame(frame_byte_array: bytes) -> np.ndarray:
    pil_img = Image.open(io.BytesIO(frame_byte_array))
    img = np.array(pil_img)
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
    return img


def detect_yolov8(frame: np.ndarray) -> list:
    model = YOLO('yolov8l.pt')
    
    results = model(frame)
    
    detections = []
    for result in results:
        boxes = result.boxes  # 获取边界框
        for box in boxes:
            x, y, w, h = box.xywh[0]
            left, top = x - w / 2, y - h / 2
            cls_str = result.names[int(box.cls[0])]  # 获取类别名称
            if cls_str == "person":
                continue
            conf = box.conf.item()
            detections.append(([left.item(), top.item(), w.item(), h.item()], conf, cls_str))

    return detections


def track_deepsort(deepsort: DeepSort, detections: list, frame: np.ndarray) -> list:
    track_list = []
    if len(detections) > 0:
        tracks = deepsort.update_tracks(detections, frame=frame)

        for track in tracks:
            x1, y1, x2, y2 = track.to_ltrb()
            track_id = track.track_id
            track_list.append((track_id, (x1, y1, x2, y2)))

    return track_list


def cross_line_detection(
        x: int, 
        current_track_list: List[Tuple[int, Tuple[int, int, int, int]]],
        former_track_list: List[Tuple[int, Tuple[int, int, int, int]]]
) -> int:
    # 从左到右加一，从右到左减一
    count = 0
    for current_track in current_track_list:
        for former_track in former_track_list:
            id_current = current_track[0]
            id_former = former_track[0]
            if id_current == id_former:
                x1_current, y1_current, x2_current, y2_current = current_track[1]
                x1_former, y1_former, x2_former, y2_former = former_track[1]
                x_current_center = (x1_current + x2_current) / 2
                x_former_center = (x1_former + x2_former) / 2
                if x1_former <= x_current_center <= x2_former and x1_current <= x_former_center <= x2_current:
                    if x_current_center < x_former_center:
                        count += 1
                    else:
                        count -= 1
    return count


class TrackMachine:
    def __init__(self, cam_id_list: List[int]):
        self.tracker_object_dict = {
            cam_id: DeepSort(max_age=30, n_init=3, nn_budget=100, max_iou_distance=0.9)
            for cam_id in cam_id_list
        }

        self.tracker_formal_track_list_dict = {
            cam_id: []
            for cam_id in cam_id_list
        }

    def image_input(self, frame_byte_array: bytes, cam_id: int) -> int:
        frame = byte_array_to_frame(frame_byte_array)
        detections = detect_yolov8(frame)
        tracker = self.tracker_object_dict[cam_id]
        track_list = track_deepsort(tracker, detections, frame)
        self.tracker_formal_track_list_dict[cam_id] = track_list
        count = cross_line_detection(100, track_list, self.tracker_formal_track_list_dict[cam_id])
        return count
