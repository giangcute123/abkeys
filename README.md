# ABKeys

Mod Fabric nhỏ cho Minecraft **1.21.11**: thêm **phím tắt** và **bảng điều khiển** cho các lệnh `/buildfarm`, để không phải gõ lệnh trong chat.

Mod chỉ gửi lệnh thay bạn. Nó **không chứa code của AutoBuilder** và không thay đổi mod đó.

## Yêu cầu

- Minecraft 1.21.11, Fabric Loader 0.16+
- Fabric API
- Mod AutoBuilder (cung cấp lệnh `/buildfarm`), kèm Litematica và Baritone

## Phím mặc định

Đổi trong **Controls → AB Keys**.

| Phím | Chức năng | Lệnh |
|------|-----------|------|
| G | Bắt đầu build | `/buildfarm start` |
| H | Hủy | `/buildfarm cancel` |
| J | Tiếp tục | `/buildfarm resume` |
| K | Xem trạng thái | `/buildfarm status` |
| L | Danh sách placement | `/buildfarm list` |
| V | Bật/tắt tự ăn | `/buildfarm autoeat toggle` |
| B | Mở bảng điều khiển | |

## Bảng điều khiển (phím B)

- Nút: Bắt đầu, Tiếp tục, Hủy, Trạng thái, Danh sách, Quét, Lấy đồ, Vào lại, Log.
- Nút Tự ăn BẬT/TẮT.
- Nút `-` / `+` chỉnh: tốc độ đặt, tốc độ lá, tầm đặt, ngưỡng ăn.
  Giá trị lúc mở bảng được đọc từ `config/autobuildfarm.json`.
- Bấm nút lệnh xong, bảng tự đóng.

## Cách dùng nhanh

1. Nạp schematic bằng Litematica (phím **M** → Load Schematics).
2. Đứng gần vị trí đặt (trong khoảng 32 block).
3. Bấm **L** để xem placement, rồi **G** để bắt đầu.

## Build

Cần JDK 21.

```
./gradlew build
```

File jar nằm ở `build/libs/`. Repo có sẵn workflow GitHub Actions (`.github/workflows/build.yml`): push code lên rồi tải jar ở mục **Artifacts** của tab Actions.

## Giấy phép

MIT
