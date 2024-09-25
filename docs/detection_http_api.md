# Detection HTTP API Documentation

## Route: `/img`

### Method
- **POST**

### Description
该路由用于接收来自指定摄像头的图像，并返回检测到的变化量。

### Request Parameters
- `cam_id` (必填): 
  - **类型**: String
  - **描述**: 摄像头的唯一标识符。

### Request Body
- **Content-Type**: `multipart/form-data`
- **图片文件**: 
  - **类型**: File
  - **描述**: PNG 格式的图像文件。

### Response
- **Content-Type**: `application/json`
- **Body**: 
```json
{
    "OK": BOOL,
    "change": INT
}
