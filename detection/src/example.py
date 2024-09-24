import cv2
import numpy as np
from ultralytics import YOLO
from deep_sort_realtime.deepsort_tracker import DeepSort
# 初始化 YOLOv8 模型
model = YOLO('yolov8l.pt')  # 使用 YOLOv8 的预训练模型

# 初始化 DeepSORT
deepsort = DeepSort(max_age=30, n_init=3, nn_budget=100, max_iou_distance=0.9)

video_path = r'C:\Data\Projects\Syn\SmartChill\detection\database\test2.mp4'

cap = cv2.VideoCapture(video_path)

if not cap.isOpened():
    print("Error opening video file")

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    # 使用 YOLOv8 进行目标检测
    results = model(frame)

    # 解析检测结果
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


    if len(detections) > 0:
        tracks = deepsort.update_tracks(detections, frame=frame)

        # 在图像上绘制跟踪结果
        for track in tracks:
            x1, y1, x2, y2 = track.to_ltrb()  # 获取跟踪框坐标
            track_id = track.track_id
            cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 2)
            cv2.putText(frame, f'ID: {track_id}', (int(x1), int(y1) - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

    # 显示结果
    cv2.imshow('Frame', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 释放视频捕获和关闭窗口
cap.release()
cv2.destroyAllWindows()
