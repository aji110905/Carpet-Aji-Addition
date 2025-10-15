import yaml
import os
import asyncio
from googletrans import Translator

LANGUAGES = {
    'en_us': 'en',
    'fr_fr': 'fr',
    'pt_br': 'pt',
    'zh_tw': 'zh-TW'
}

translator = Translator()

def load_yaml(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def save_yaml(data, file_path):
    with open(file_path, 'w', encoding='utf-8') as f:
        yaml.dump(data, f, allow_unicode=True, default_flow_style=False, indent=2)

async def translate_text(text, target_lang, source_lang='zh'):
    try:
        result = await translator.translate(text, src=source_lang, dest=target_lang)
        await asyncio.sleep(0.1)
        return result.text
    except Exception as e:
        print(f"翻译 '{text}' 到 {target_lang} 时出错: {e}")
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
    for version_dir in os.listdir(resources_dir):
        version_path = os.path.join(resources_dir, version_dir)
        if not os.path.isdir(version_path):
            continue
            
        lang_dir = os.path.join(version_path, "lang")
        if not os.path.exists(lang_dir):
            continue

        zh_cn_file = os.path.join(lang_dir, "zh_cn.yml")
        if not os.path.exists(zh_cn_file):
            continue

        zh_cn_data = load_yaml(zh_cn_file)

        for lang_code, lang_short in LANGUAGES.items():
            print(f"正在翻译 {version_dir} 版本到 {lang_code}...")
            translated_data = await translate_dict(zh_cn_data, lang_short)

            output_file = os.path.join(lang_dir, f"{lang_code}.yml")
            save_yaml(translated_data, output_file)
            print(f"已生成 {lang_code}.yml for {version_dir}")

async def main():
    resources_dir = "../resources"
    await process_resource_directory(resources_dir)
    print("翻译完成!")

if __name__ == "__main__":
    asyncio.run(main())