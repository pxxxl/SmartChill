#include <iostream>
#include <cstdlib>
#include <filesystem>
#include <curl/curl.h>
#include <chrono>
#include <thread>

int main(int argc, char** argv) {
    const char* photo_path = "/home/Pi/image01.jpg"; // 照片保存路径
    const char* capture_cmd = "fswebcam /dev/video0 --no-banner -r 640x480 /home/Pi/image01.jpg"; // fswebcam 命令
    const char* url = "http://192.168.5.5:18080/node/image"; // 上传目标URL

    int iterations = 0; // 计数器
    const double delay_seconds = 0.1; // 延时0.1秒
    const int max_iterations = 100; // 最大循环次数（可修改）
    
    // 开始时间
    auto start_time = std::chrono::high_resolution_clock::now();

    // 初始化libcurl
    CURL* curl = curl_easy_init();
    if (!curl) {
        std::cerr << "Failed to initialize curl" << std::endl;
        return 1;
    }

    // 主循环
    for (iterations = 0; iterations < max_iterations; ) {
        std::cout << "Iteration: " << iterations + 1 << std::endl;

        // 执行命令生成照片
        int ret = std::system(capture_cmd);
        if (ret != 0) {
            std::cerr << "Error capturing photo. Command exited with code: " << ret << std::endl;
            continue;
        }

        // 检查照片是否生成成功
        if (!std::filesystem::exists(photo_path)) {
            std::cerr << "Photo not found at: " << photo_path << std::endl;
            continue;
        }

        // 创建表单数据
        curl_mime* mime = curl_mime_init(curl);
        curl_mimepart* part = curl_mime_addpart(mime);
        curl_mime_name(part, "file");
        curl_mime_filedata(part, photo_path);

        // 设置上传的URL和表单数据
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_MIMEPOST, mime);

        // 发送POST请求
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Upload failed: " << curl_easy_strerror(res) << std::endl;
        } else {
            std::cout << "Photo uploaded successfully" << std::endl;
        }

        // 清理表单数据
        curl_mime_free(mime);

        // 延时0.1秒
        // std::this_thread::sleep_for(std::chrono::milliseconds(static_cast<int>(delay_seconds * 1000)));
    }

    // 结束时间
    auto end_time = std::chrono::high_resolution_clock::now();

    // 计算总运行时间
    std::chrono::duration<double> elapsed_time = end_time - start_time;

    // 计算并显示FPS
    double fps = iterations / elapsed_time.count();
    std::cout << "Final FPS: " << fps << " frames per second" << std::endl;

    // 清理libcurl
    curl_easy_cleanup(curl);

    return 0;
}
