import os
import yaml

def load_yaml(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def save_yaml(data, file_path):
    with open(file_path, 'w', encoding='utf-8') as f:
        yaml.dump(data, f, allow_unicode=True, default_flow_style=False, indent=2)

def merge_dicts(dict1, dict2):
    result = dict1.copy()
    for key, value in dict2.items():
        if key in result and isinstance(result[key], dict) and isinstance(value, dict):
            result[key] = merge_dicts(result[key], value)
        else:
            result[key] = value
    return result

def merge_lang_files_to_versions(version_list):
    resources_dir = "../resources"
    dev_dir = os.path.join(resources_dir, "dev")
    dev_lang_dir = os.path.join(dev_dir, "lang")
    if not os.path.exists(dev_lang_dir):
        print("错误: dev/lang目录不存在")
        return
    
    dev_lang_files = [f for f in os.listdir(dev_lang_dir) if f.endswith(".yml")]
    if not dev_lang_files:
        print("警告: dev目录中没有找到语言文件")
        return

    for version in version_list:
        if version == "dev":
            print("错误: 不能将语言文件合并到dev目录")
            continue
            
        version_dir = os.path.join(resources_dir, version)

        if not os.path.exists(version_dir):
            print(f"错误: 目录 {version_dir} 不存在")
            return

        version_lang_dir = os.path.join(version_dir, "lang")
        if not os.path.exists(version_lang_dir):
            os.makedirs(version_lang_dir)
            print(f"创建目录: {version_lang_dir}")

        for lang_file in dev_lang_files:
            dev_file_path = os.path.join(dev_lang_dir, lang_file)
            version_file_path = os.path.join(version_lang_dir, lang_file)

            dev_data = load_yaml(dev_file_path)

            if os.path.exists(version_file_path):
                version_data = load_yaml(version_file_path)
                merged_data = merge_dicts(version_data, dev_data)
                print(f"合并 {lang_file} 到 {version} 目录")
            else:
                merged_data = dev_data
                print(f"复制 {lang_file} 到 {version} 目录")
            
            # 保存合并后的数据
            save_yaml(merged_data, version_file_path)
    
    print("语言文件合并完成!")

def main():
    print("语言文件合并工具")
    print("请输入目标版本目录名称，多个目录请用英文逗号分隔:")

    user_input = input().strip()
    
    if not user_input:
        print("错误: 输入不能为空")
        return

    version_list = [version.strip() for version in user_input.split(",") if version.strip()]
    
    if not version_list:
        print("错误: 没有有效的版本目录名称")
        return

    if len(version_list) != len(set(version_list)):
        print("错误: 输入中包含重复的目录名称")
        return

    for version in version_list:
        if version == "dev":
            print("错误: 不能包含dev目录")
            return
            
        version_dir = os.path.join("../resources", version)

        if not os.path.exists(version_dir):
            print(f"错误: 目录 {version_dir} 不存在")
            return

    merge_lang_files_to_versions(version_list)

if __name__ == "__main__":
    main()