const char *http_url = "http://192.168.5.5:18080/node/temperature";

#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <unistd.h>

#include <wiringPi.h>
#include <iostream>
#include <string>
#include <curl/curl.h>
#include <jsoncpp/json/json.h>

typedef unsigned char uint8;
typedef unsigned int  uint16;
typedef unsigned long uint32;

uint32 databuf;

/*
//初始化引脚
//上电1s内不操作，维持电平稳定
*/
void GPIO_init(int gpio_pin)
{
	pinMode(gpio_pin, OUTPUT); // set mode to output
	digitalWrite(gpio_pin, 1); // output a high level
	
    sleep(1);
	
	return;
}

/*
//起始信号
	1.主机初状态是高电平，要超过1s稳定电平
	2.主机再拉低延时18ms-30ms
	3.主机末状态是高电平，等待
*/
void DHT11_start(int gpio_pin)
{	
	pinMode(gpio_pin, OUTPUT);
	digitalWrite(gpio_pin, 0);
	
	delay(25);
	
	digitalWrite(gpio_pin, 1);
	pinMode(gpio_pin, INPUT);
	pullUpDnControl(gpio_pin, PUD_UP);	//当引脚被配置为输入(INPUT)模式，使用函数pullUpDnControl来激活其内部的上拉电阻或下拉电阻
	
	delayMicroseconds(27);
	
	return;
}

/*
//主机接受数据
	1.主机接受到从机回复的响应信号
	2.格式0——54us的低电平+23到27us的高电平
	  格式1——54us的低电平+68到74us的高电平
	3.思路：从识别到低电平开始，然后去除掉掉前面54秒的低电平还有
*/
uint8 DHT11_read(int gpio_pin)
{
	uint8 crc, i;
	
	if (0 == digitalRead(gpio_pin))			//主机接收到从机发送的响应信号（低电平）
	{
		while(!digitalRead(gpio_pin));		//主机接收到从机发送的响应信号（高电平）
		
		for (i = 0; i < 32; i++)
		{
			while(digitalRead(gpio_pin));	//数据位开始的54us低电平
			while(!digitalRead(gpio_pin));	//数据位开始的高电平就开始
			
			delayMicroseconds(32);			//跳过位数据，32us已经是数据0和数据1的差距点
			
			databuf *= 2;
			
			if (digitalRead(gpio_pin) == 1)
			{
				databuf++;
			}
		}
		
		for (i = 0; i < 8; i++)
        {
            while (digitalRead(gpio_pin));
            while (!digitalRead(gpio_pin));
			
            delayMicroseconds(32);
			
            crc *= 2;  
            if (digitalRead(gpio_pin) == 1)
            {
                crc++;
            }
        }
		return 1;
	}
	else
	{
		return 0;
	}
}


void send_temp_http(float temperature, const char *url) {
    // 初始化 libcurl
    CURL *curl = curl_easy_init();
    if (!curl) {
        std::cerr << "Failed to initialize libcurl" << std::endl;
        return;
    }

    // 创建 JSON 数据
    Json::Value jsonData;
    jsonData["temperature"] = std::to_string(temperature);
	jsonData["id"] = std::to_string(3);
    Json::StreamWriterBuilder writer;
    std::string jsonString = Json::writeString(writer, jsonData);

    // 配置 libcurl
    curl_easy_setopt(curl, CURLOPT_URL, url);
    curl_easy_setopt(curl, CURLOPT_POST, 1L);
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonString.c_str());
    curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonString.size());

    // 设置 HTTP 头
    struct curl_slist *headers = nullptr;
    headers = curl_slist_append(headers, "Content-Type: application/json");
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    // 执行请求
    CURLcode res = curl_easy_perform(curl);
    if (res != CURLE_OK) {
        std::cerr << "CURL failed: " << curl_easy_strerror(res) << std::endl;
    } else {
        std::cout << "Data sent successfully to " << url << std::endl;
    }

    // 清理资源
    curl_slist_free_all(headers);
    curl_easy_cleanup(curl);
}


int main(int argc, char *argv[])
{
	if (2 != argc)
	{
		printf("Usge: %s <gpio_pin>\n", argv[0]);
		return 0;
	}
	
	if (-1 == wiringPiSetup())
	{
		printf("Setup WiringPi failed!\n");
		
		return 1;
	}
	
	while(1)
	{
		GPIO_init(atoi(argv[1]));
		
		DHT11_start(atoi(argv[1]));
	 
		if (DHT11_read(atoi(argv[1])))
		{
			printf("RH:%d.%d\n", (databuf >> 24) & 0xff, (databuf >> 16) & 0xff); 
			printf("TMP:%d.%d\n", (databuf >> 8) & 0xff, databuf & 0xff);

            float temperature = ((databuf >> 8) & 0xff) + (databuf & 0xff) / 100.0f;
            send_temp_http(temperature, http_url);
		
			databuf = 0;
		}
		else
        {
            printf("Sensor dosent ans!\n");
            databuf = 0;
        }
		
		sleep(10);
	}
	
	return 0;
}
