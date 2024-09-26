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

# 示例用法
video_file = r'C:/Data/Projects/Syn/SmartChill/detection/database/mp4/test.mp4'
output_folder = r'C:\Data\Projects\Syn\SmartChill\detection\database\imgs\test'
extract_frames(video_file, output_folder, fps=5)  # 每秒提取一帧
