import os
import requests
import argparse

# get the image dir path as -i argument
parser = argparse.ArgumentParser(description='Extract frames from a video file')
parser.add_argument('-i', '--image_dir', type=str, help='Path to the image directory', required=True)
args = parser.parse_args()

image_directory = args.image_dir

# API地址
url = "http://127.0.0.1:5987/img"  # 替换为实际API地址

# 摄像头ID
cam_id = 0

# 按名字顺序遍历图片
for image_name in sorted(os.listdir(image_directory)):
    # 检查是否为PNG文件
    if image_name.lower().endswith('.png'):
        image_path = os.path.join(image_directory, image_name)
        
        # 读取并发送图片
        with open(image_path, 'rb') as img:
            files = {
                'file': (image_name, img, 'image/png'),
            }
            data = {
                'cam_id': cam_id,
            }
            
            # 发送POST请求
            response = requests.post(url, files=files, data=data)
            
            # 处理响应
            if response.status_code == 200:
                json_response = response.json()
                print(f"{image_name}: {json_response}")
            else:
                print(f"{image_name}: 请求失败，状态码: {response.status_code}")
