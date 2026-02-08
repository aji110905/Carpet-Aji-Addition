import yaml
import json
import os

def process_language(d, parent_key='', sep='.'):
    items = []
    for k, v in d.items():
        new_key = f"{parent_key}{sep}{k}" if parent_key else k
        if isinstance(v, dict):
            items.extend(process_language(v, new_key, sep=sep).items())
        else:
            items.append((new_key, v))
    return dict(items)

def main():
    lang_dir = "../resources/lang"
    output_dir = "../../main/resources/assets/carpetajiaddition/lang"

    os.makedirs(output_dir, exist_ok=True)

    for lang_file in os.listdir(lang_dir):
        if not lang_file.endswith(".yml"):
            continue

        lang_file_path = os.path.join(lang_dir, lang_file)
        with open(lang_file_path, 'r', encoding='utf-8') as f:
            lang_data = yaml.safe_load(f)

        flat_lang_data = process_language(lang_data)

        output_filename = lang_file.replace(".yml", ".json")
        output_file_path = os.path.join(output_dir, output_filename)
        with open(output_file_path, 'w', encoding='utf-8') as f:
            json.dump(flat_lang_data, f, ensure_ascii=False, indent=2)
    print("成功")

if __name__ == "__main__":
    main()