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
    lang_root = "../resources"
    output_root = "../../../"
    version_languages = {}
    for version_dir in os.listdir(lang_root):
        version_path = os.path.join(lang_root, version_dir)
        if not os.path.isdir(version_path):
            continue
        versions_file = os.path.join(version_path, "versions.json")
        if not os.path.exists(versions_file):
            continue
        with open(versions_file, 'r', encoding='utf-8') as f:
            versions = json.load(f)
        lang_dir = os.path.join(version_path, "lang")
        if not os.path.exists(lang_dir):
            continue
        language_files = {}
        for lang_file in os.listdir(lang_dir):
            if not lang_file.endswith(".yml"):
                continue
            lang_file_path = os.path.join(lang_dir, lang_file)
            with open(lang_file_path, 'r', encoding='utf-8') as f:
                lang_data = yaml.safe_load(f)
            flat_lang_data = process_language(lang_data)
            language_files[lang_file] = flat_lang_data
        for version in versions:
            if version not in version_languages:
                version_languages[version] = {}
            for lang_file, flat_lang_data in language_files.items():
                if lang_file not in version_languages[version]:
                    version_languages[version][lang_file] = {}
                version_languages[version][lang_file].update(flat_lang_data)
    for version, language_files in version_languages.items():
        for lang_file, flat_lang_data in language_files.items():
            target_dir = os.path.join(output_root, "src", "main", "resources", "assets", "carpetajiaddition", "lang")
            os.makedirs(target_dir, exist_ok=True)
            output_filename = lang_file.replace(".yml", ".json")
            output_file_path = os.path.join(target_dir, output_filename)
            with open(output_file_path, 'w', encoding='utf-8') as f:
                json.dump(flat_lang_data, f, ensure_ascii=False, indent=2)
    print("成功")

if __name__ == "__main__":
    main()