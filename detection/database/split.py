import subprocess
import os

def extract_frames(video_path, output_dir, fps=1):
    # 创建输出目录（如果不存在）
    os.makedirs(output_dir, exist_ok=True)
    
    # 使用ffmpeg命令提取帧
    command = [
        'ffmpeg',
        '-i', video_path,                   # 输入文件
        '-vf', f'fps={fps}',                # 设置帧率
        os.path.join(output_dir, 'frame_%04d.png')  # 输出文件名格式
    ]
    
    try:
        subprocess.run(command, check=True)
        print(f"Frames extracted to {output_dir}")
    except subprocess.CalledProcessError as e:
        print(f"Error during frame extraction: {e}")

import argparse

parser = argparse.ArgumentParser(description='Extract frames from a video file')
parser.add_argument('-i', '--video_file', type=str, help='Path to the video file', required=True)
parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder', required=True)
args = parser.parse_args()

video_file = args.video_file
output_folder = args.output_folder

extract_frames(video_file, output_folder, fps=5)  # 每秒提取一帧

# usage: -i ./mp4/test.mp4 -o ./img/test