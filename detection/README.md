# Bottle detection 使用手册

## 1. 简介

本文档介绍了如何使用Bottle detection模块进行瓶子检测。

Bottle detection 主程序是HTTP服务器，用于接收来自指定摄像头的图像，并检测瓶子的移动，将跨越边界线的瓶子数量返回。

## 2. 使用方法

推荐使用docker启动服务，以下是所有docker相关语句。

你可以选择直接下载镜像，也可以从源码构建镜像。

### 2.1. 下载镜像

下载镜像：
```bash
docker pull cozardez/bottle_det:latest
```

### 2.2. 从源码构建镜像

构建镜像：（cozardez是我的dockerhub用户名，可以换）
```bash
cd detection
docker build -t cozardez/bottle_det:latest .
```

上传镜像：
```bash
docker push cozardez/bottle_det:latest
```

### 2.3. 启动容器

启动容器(注意将C:/Data/Projects/Syn/SmartChill替换成你的项目路径)
```bash
docker run -p 5987:5987 -v C:/Data/Projects/Syn/SmartChill/detection/config.json:/app/src/config.json -v C:/Data/Projects/Syn/SmartChill/detection/detection.log:/app/src/detection.log cozardez/bottle_det:latest 
```

配置文件的有效格式如下：
```json
{
    "width": 640,
    "height": 480,
    "cam_id": ["0", "1"]
}
```

## 3. API

本服务使用HTTP协议，只提供一个API接口。

### 3.1. 探测瓶子

#### Route: `/img`

#### Method
- **POST**

#### Description
该路由用于接收来自指定摄像头的图像，并返回一个变化量。变化量初始为0，当某个瓶子在这一帧从中线左边移动到右边时，变化量加1；当某个瓶子在这一帧从中线右边移动到左边时，变化量减1。这个变化量可以反映冰柜里饮料数量的变化。

#### Request Parameters
- `cam_id` (必填): 
  - **类型**: String
  - **描述**: 摄像头的唯一标识符。

#### Request Body
- **Content-Type**: `multipart/form-data`
- **图片文件**: 
  - **类型**: File
  - **描述**: PNG 格式的图像文件。

#### Response
- **Content-Type**: `application/json`
- **Body**: 
```json
{
    "OK": BOOL,
    "change": INT
}
```

## 4. 示例

代码仓库里附带了一个mp4文件，你可以用它来测试这个服务。

**要使用此测试脚本，你需要安装ffmpeg，并确保它在命令行可用**

先把mp4文件切分成图片
```bash
python detection/database/split.py -i ./database/mp4/test.mp4 -o ./database/img/test
```

此时 database/imgs 文件夹里会有图片文件夹。

然后启动服务(注意将C:/Data/Projects/Syn/SmartChill替换成你的项目路径)
```bash
docker run -p 5987:5987 -v C:/Data/Projects/Syn/SmartChill/detection/config.json:/app/src/config.json -v C:/Data/Projects/Syn/SmartChill/detection/detection.log:/app/src/detection.log cozardez/bottle_det:latest 
```

然后运行测试脚本
```bash
python detection/database/feeder.py -i detection/database/img/test
```

你会看到docker服务输出：
```
0: 480x640 1 person, 1 cup, 3061.3ms
Speed: 3.2ms preprocess, 3061.3ms inference, 7.1ms postprocess per image at shape (1, 3, 480, 640)
2024-09-27 12:33:23,539 - INFO - Detection complete
2024-09-27 12:33:23,540 - INFO - Detected cup with confidence 0.895628809928894
2024-09-27 12:33:23,540 - INFO - Bounding box: 192.62042236328125 88.6787109375 236.4798583984375 218.23483276367188
2024-09-27 12:33:23,544 - INFO - Begin tracking objects
2024-09-27 12:33:23,633 - INFO - Tracking object 1 at 192.62042236328125 88.6787109375 429.10028076171875 306.9135437011719
2024-09-27 12:33:23,633 - INFO - Cross line detection: 0
2024-09-27 12:33:23,634 - INFO - Image uploaded, 0 changes detected
2024-09-27 12:33:23,635 - INFO - 172.17.0.1 - - [27/Sep/2024 12:33:23] "POST /img HTTP/1.1" 200 -
2024-09-27 12:33:23,647 - INFO - Image upload request received
2024-09-27 12:33:23,650 - INFO - Converting byte array to frame
2024-09-27 12:33:23,661 - INFO - Byte array converted to frame
2024-09-27 12:33:23,661 - INFO - Detecting objects

2024-09-27 12:33:27,681 - INFO - Detection complete

...(后面省略)
```

你会看到测试脚本输出：
```
frame_0001.png: {'OK': True, 'change': 0}
frame_0002.png: {'OK': True, 'change': 0}
frame_0003.png: {'OK': True, 'change': 0}
frame_0004.png: {'OK': True, 'change': 0}
frame_0005.png: {'OK': True, 'change': 0}
frame_0006.png: {'OK': True, 'change': 0}
frame_0007.png: {'OK': True, 'change': 0}
frame_0008.png: {'OK': True, 'change': 0}
frame_0009.png: {'OK': True, 'change': 1}
frame_0010.png: {'OK': True, 'change': 0}
frame_0011.png: {'OK': True, 'change': 0}
frame_0012.png: {'OK': True, 'change': -1}
frame_0013.png: {'OK': True, 'change': 0}
frame_0014.png: {'OK': True, 'change': 0}
frame_0015.png: {'OK': True, 'change': 0}
frame_0016.png: {'OK': True, 'change': 0}
```

其中`change`字段表示这一帧的变化量，第9帧是change=1，表示有一个瓶子从左边移动到右边；第12帧是change=-1，表示有一个瓶子从右边移动到左边。


## 5. 修改配置与查看日志

路径/detection/config.json是配置文件，你可以修改它来更改摄像头的参数。在容器启动时，会将这个文件挂载到容器内部，所以你可以在容器外部修改这个文件，然后重启容器。

路径/detection/detection.log是日志文件，你可以查看它来了解服务的运行情况。在容器启动时，会将这个文件挂载到容器内部，所以你可以在容器外部查看这个文件。
