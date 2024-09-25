from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/img', methods=['POST'])
def upload_image():
    success = True
    change = 0
    try:
        # 从请求中获取 cam_id 和图片文件
        cam_id = request.form.get('cam_id')
        image_file = request.files.get('image')

        # 在这里添加图像处理和变化检测的逻辑

        # 返回 JSON 响应
    except Exception as e:
        success = False
        print(e)

    return jsonify({"change": 0, "OK": success})  # 将 0 替换为实际变化量

if __name__ == '__main__':
    app.run(debug=True)
