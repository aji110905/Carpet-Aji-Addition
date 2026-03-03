import yaml
import os
import asyncio
import aiohttp
import hashlib
import time
from tqdm import tqdm

# 百度翻译配置 - 请替换为你自己的 APP ID 和 Secret Key
BAIDU_APP_ID = "你的百度翻译APP ID"
BAIDU_SECRET_KEY = "你的百度翻译Secret Key"

LANGUAGES = {
    'en_us': 'en',
    'fr_fr': 'fra',
    'pt_br': 'pt',
    'zh_tw': 'cht'
}

progress_bar = None
total_text_count = 0
current_count = 0

def load_yaml(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def save_yaml(data, file_path):
    with open(file_path, 'w', encoding='utf-8') as f:
        yaml.dump(data, f, allow_unicode=True, default_flow_style=False, indent=2)

def count_text_items(data):
    count = 0
    if isinstance(data, dict):
        for value in data.values():
            count += count_text_items(value)
    elif isinstance(data, list):
        for item in data:
            count += count_text_items(item)
    elif isinstance(data, str) and data.strip():
        count += 1
    return count

async def translate_text(text, target_lang, source_lang='zh'):
    global current_count, progress_bar
    if not text.strip():
        return text
    try:
        api_url = "https://fanyi-api.baidu.com/api/trans/vip/translate"
        salt = str(int(time.time()))
        sign_str = BAIDU_APP_ID + text + salt + BAIDU_SECRET_KEY
        sign = hashlib.md5(sign_str.encode()).hexdigest()
        params = {
            'q': text,
            'from': source_lang,
            'to': target_lang,
            'appid': BAIDU_APP_ID,
            'salt': salt,
            'sign': sign
        }
        async with aiohttp.ClientSession() as session:
            async with session.get(api_url, params=params) as response:
                if response.status != 200:
                    raise Exception(f"API请求失败，状态码：{response.status}")
                result = await response.json()
                if 'error_code' in result:
                    raise Exception(f"翻译API错误：{result['error_code']} - {result.get('error_msg', '未知错误')}")
                translated_text = ''.join([item['dst'] for item in result['trans_result']])
                await asyncio.sleep(1)
                current_count += 1
                if progress_bar:
                    progress_bar.update(1)
                return translated_text
    except Exception as e:
        print(f"\n翻译 '{text}' 到 {target_lang} 时出错: {e}")
        current_count += 1
        if progress_bar:
            progress_bar.update(1)
        return text

async def translate_dict(data, target_lang):
    if isinstance(data, dict):
        translated_dict = {}
        for key, value in data.items():
            translated_dict[key] = await translate_dict(value, target_lang)
        return translated_dict
    elif isinstance(data, str):
        return await translate_text(data, target_lang)
    elif isinstance(data, list):
        translated_list = []
        for item in data:
            translated_list.append(await translate_dict(item, target_lang))
        return translated_list
    else:
        return data

async def process_resource_directory(resources_dir):
    global progress_bar, total_text_count, current_count
    lang_dir = os.path.join(resources_dir, "lang")
    if not os.path.exists(lang_dir):
        print(f"语言目录不存在: {lang_dir}")
        return
    zh_cn_file = os.path.join(lang_dir, "zh_cn.yml")
    if not os.path.exists(zh_cn_file):
        print(f"未找到源语言文件: {zh_cn_file}")
        return
    zh_cn_data = load_yaml(zh_cn_file)
    single_lang_count = count_text_items(zh_cn_data)
    total_text_count = single_lang_count * len(LANGUAGES)
    print(f"开始翻译，总计需要处理 {total_text_count} 个文本条目")
    progress_bar = tqdm(
        total=total_text_count,
        desc="整体翻译进度",
        unit="条目",
        ncols=80,
        colour="green"
    )
    try:
        for lang_code, lang_short in LANGUAGES.items():
            current_count_for_lang = 0
            progress_bar.set_postfix({"当前语言": lang_code})
            print(f"\n正在翻译到 {lang_code}...")
            translated_data = await translate_dict(zh_cn_data, lang_short)
            output_file = os.path.join(lang_dir, f"{lang_code}.yml")
            save_yaml(translated_data, output_file)
            print(f"已生成 {lang_code}.yml")
    finally:
        progress_bar.close()
        progress_bar = None

async def main():
    resources_dir = "../resources"
    await process_resource_directory(resources_dir)
    print("\n翻译完成!")

if __name__ == "__main__":
    if BAIDU_APP_ID == "你的百度翻译APP ID" or BAIDU_SECRET_KEY == "你的百度翻译Secret Key":
        print("错误：请先填写你的百度翻译APP ID和Secret Key！")
    else:
        asyncio.run(main())