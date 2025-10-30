#!/bin/bash

###############################################################################
# PlantUML Server - 打赏功能快速配置脚本
###############################################################################

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     PlantUML Server - 打赏功能配置工具            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""

# 检查文件是否存在
FILE1="src/main/webapp/components/modals/donate/donate.js"
FILE2="src/main/webapp/js/ai-app.js"

if [ ! -f "$FILE1" ]; then
    echo -e "${RED}错误: 找不到文件 $FILE1${NC}"
    echo -e "${YELLOW}请确保您在项目根目录运行此脚本${NC}"
    exit 1
fi

if [ ! -f "$FILE2" ]; then
    echo -e "${RED}错误: 找不到文件 $FILE2${NC}"
    echo -e "${YELLOW}请确保您在项目根目录运行此脚本${NC}"
    exit 1
fi

# 获取用户输入
echo -e "${GREEN}请输入您的微信号:${NC}"
read -p "微信号: " WECHAT_ID

if [ -z "$WECHAT_ID" ]; then
    echo -e "${RED}错误: 微信号不能为空${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}您输入的微信号是: ${WECHAT_ID}${NC}"
read -p "确认无误？(y/n) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}已取消配置${NC}"
    exit 1
fi

# 备份原文件
echo ""
echo -e "${BLUE}正在备份原文件...${NC}"
cp "$FILE1" "$FILE1.backup"
cp "$FILE2" "$FILE2.backup"
echo -e "${GREEN}✓ 备份完成${NC}"

# 替换微信号 - 主页面
echo -e "${BLUE}正在配置主页面...${NC}"
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s/const WECHAT_ID = \".*\";/const WECHAT_ID = \"$WECHAT_ID\";/" "$FILE1"
else
    # Linux
    sed -i "s/const WECHAT_ID = \".*\";/const WECHAT_ID = \"$WECHAT_ID\";/" "$FILE1"
fi
echo -e "${GREEN}✓ 主页面配置完成${NC}"

# 替换微信号 - AI页面
echo -e "${BLUE}正在配置AI页面...${NC}"
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s/const DONATE_WECHAT_ID = \".*\";/const DONATE_WECHAT_ID = \"$WECHAT_ID\";/" "$FILE2"
else
    # Linux
    sed -i "s/const DONATE_WECHAT_ID = \".*\";/const DONATE_WECHAT_ID = \"$WECHAT_ID\";/" "$FILE2"
fi
echo -e "${GREEN}✓ AI页面配置完成${NC}"

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║           ✓ 配置成功！                            ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════╝${NC}"
echo ""

# 检查是否有微信收款码
QRCODE_FILE="src/main/webapp/assets/wechat-pay.png"
if [ -f "$QRCODE_FILE" ]; then
    echo -e "${GREEN}✓ 检测到微信收款码图片${NC}"
else
    echo -e "${YELLOW}⚠ 未检测到微信收款码图片${NC}"
    echo -e "${YELLOW}  请将您的收款码图片保存为:${NC}"
    echo -e "${YELLOW}  $QRCODE_FILE${NC}"
fi

echo ""
echo -e "${BLUE}后续步骤:${NC}"
echo -e "  1. 如需添加收款码图片，请放置到: ${YELLOW}$QRCODE_FILE${NC}"
echo -e "  2. 重新编译项目: ${YELLOW}mvn clean package${NC}"
echo -e "  3. 启动服务器: ${YELLOW}mvn jetty:run${NC}"
echo ""
echo -e "${GREEN}备份文件位置:${NC}"
echo -e "  - $FILE1.backup"
echo -e "  - $FILE2.backup"
echo ""
echo -e "${BLUE}如需恢复原配置，运行:${NC}"
echo -e "  mv $FILE1.backup $FILE1"
echo -e "  mv $FILE2.backup $FILE2"
echo ""

exit 0

