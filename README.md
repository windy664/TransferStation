<div align="center">

<img src="assets/banner.svg" alt="TransferStation" width="100%">

# TransferStation

**物品中转暂存仓** —— 玩家把物品放入 GUI 暂存，关闭后自动经 MailBox 邮件投递；支持按物品分级收税、每日限额与 PlaceholderAPI 变量。

![Platform](https://img.shields.io/badge/Platform-Paper-62bda8?style=flat-square&logo=minecraft&logoColor=white)
![Minecraft](https://img.shields.io/badge/MC-1.21.x-8a9a5b?style=flat-square)
![Java](https://img.shields.io/badge/Java-25-f89820?style=flat-square&logo=openjdk&logoColor=white)
![Depends](https://img.shields.io/badge/depend-MailBox-e2b90f?style=flat-square)

[功能](#-功能) · [命令与权限](#-命令与权限) · [配置](#️-配置) · [构建](#-构建)

</div>

## ✨ 功能

- **GUI 中转仓**：6 行大界面（标题、行数可配），放入物品后关闭界面即自动打包投递
- **邮件投递**：通过 [MailBox](https://github.com/windy664/MailBox) 把暂存物品寄出，投递留痕（`TransferLogger`）
- **分级税收**：按物品正则分组收税 —— 贵重品（钻石/绿宝石/下界合金…）5%、矿石与盔甲工具 3%、其余默认 2%，税率与分组全部可配
- **税收不足处理**：`insufficient-action` 支持 `REFUSE` 等策略，钱不够就拒收
- **每日限额**：按玩家记录每日使用次数，`transfer.limit.unlimited` 权限可豁免
- **PlaceholderAPI**：内置 `%transfer_*%` 变量扩展（`TransferExpansion`）

## 📜 命令与权限

| 命令/权限 | 说明 | 默认 |
|------|------|------|
| `/transfer`（GUI 打开） | 打开中转暂存仓 | `transfer.use`（所有玩家） |
| `transfer.admin` | 重载等管理操作 | OP |
| `transfer.limit.unlimited` | 不受每日次数限制 | 无 |

## ⚙️ 配置

`config.yml` 核心段：

```yaml
gui:
  title: "&3✉ &b中转暂存仓 &8- &7放入后关闭自动投递"
  rows: 6

tax:
  default-rate: 2.0
  insufficient-action: "REFUSE"
  groups:
    precious:
      materials: ["^DIAMOND$", "^EMERALD$", "^NETHERITE_INGOT$", "..."]
      rate: 5.0
    ores:
      materials: ["^RAW_IRON$", "^.*_ORE$", "..."]
      rate: 3.0
```

依赖：MailBox（必须，`load: BEFORE`）、PlaceholderAPI（可选）。

## 🔨 构建

```bash
./gradlew build
```
