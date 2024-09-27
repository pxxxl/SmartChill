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

启动容器：（/path/to/your/config.json是你的配置文件路径）
```bash
docker run -p 5987:5987 cozardez/bottle_det:latest -v ./config.json:/app/src/config.json -v ./detection.log:/app/src/detection.log
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
cd detection
python database/split.py -i ./database/mp4/test.mp4 -o ./database/img/test
```

此时 database/imgs 文件夹里会有图片文件夹。

然后启动服务
```bash
docker run -p 5987:5987 cozardez/bottle_det:latest -v ./config.json:/app/src/config.json -v ./detection.log:/app/src/detection.log
```

然后运行测试脚本
```bash
python database/feeder.py -i ./database/img/test
```

你会看到输出：
```
