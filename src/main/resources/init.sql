-- CreateTable
CREATE TABLE `account` (
    `id` BINARY(16) NOT NULL,
    `email` VARCHAR(191) NOT NULL,
    `name` VARCHAR(191) NULL,
    `password` VARCHAR(191) NOT NULL,
    `role` ENUM('Customer', 'Admin') NOT NULL,

    UNIQUE INDEX `account_email_key`(`email`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `customer` (
    `account_id` BINARY(16) NOT NULL,
    `gender` ENUM('Male', 'Female', 'Other') NULL,
    `born_date` DATETIME(3) NULL,
    `phone` VARCHAR(191) NULL,
    `avatar` VARCHAR(191) NULL,

    PRIMARY KEY (`account_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `admin` (
    `account_id` BINARY(16) NOT NULL,

    PRIMARY KEY (`account_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `laptop_model` (
    `id` BINARY(16) NOT NULL,
    `name` VARCHAR(191) NOT NULL,
    `brand` VARCHAR(191) NOT NULL,
    `cpu` VARCHAR(191) NOT NULL,
    `ram` VARCHAR(191) NOT NULL,
    `gpu` VARCHAR(191) NOT NULL,
    `storage` VARCHAR(191) NOT NULL,
    `display` VARCHAR(191) NOT NULL,
    `color` ENUM('Red', 'Blue', 'Green', 'Black', 'White', 'Silver', 'Gold') NOT NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `description` VARCHAR(191) NULL,

    INDEX `laptop_model_cpu_idx`(`cpu`),
    INDEX `laptop_model_ram_idx`(`ram`),
    INDEX `laptop_model_brand_idx`(`brand`),
    INDEX `laptop_model_cpu_ram_idx`(`cpu`, `ram`),
    INDEX `laptop_model_cpu_ram_brand_idx`(`cpu`, `ram`, `brand`),
    INDEX `laptop_model_cpu_ram_brand_price_idx`(`cpu`, `ram`, `brand`, `price`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `laptop_on_sale` (
    `laptop_model_id` BINARY(16) NOT NULL,
    `sale_id` BINARY(16) NOT NULL,

    PRIMARY KEY (`laptop_model_id`, `sale_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `sale` (
    `id` BINARY(16) NOT NULL,
    `event_description` VARCHAR(191) NULL,
    `start_at` DATETIME(3) NOT NULL,
    `end_at` DATETIME(3) NOT NULL,
    `discount` DOUBLE NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `laptop` (
    `mac_id` BINARY(16) NOT NULL,
    `MFG` DATETIME(3) NOT NULL,
    `model_id` BINARY(16) NOT NULL,
    `status` ENUM('Available', 'SoldOut', 'Maintenance') NOT NULL DEFAULT 'Available',

    INDEX `laptop_status_idx`(`status`),
    INDEX `laptop_model_id_idx`(`model_id`),
    PRIMARY KEY (`mac_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `order` (
    `id` BINARY(16) NOT NULL,
    `customer_id` BINARY(16) NOT NULL,
    `status` ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') NOT NULL DEFAULT 'Pending',
    `address` VARCHAR(191) NOT NULL,
    `date_create` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `delivery_cost` DECIMAL(10, 2) NOT NULL,
    `final_price` DECIMAL(10, 2) NOT NULL,

    INDEX `order_customer_id_idx`(`customer_id`),
    INDEX `order_status_idx`(`status`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `order_detail` (
    `id` BINARY(16) NOT NULL,
    `order_id` BINARY(16) NOT NULL,
    `laptop_model_id` BINARY(16) NOT NULL,
    `quantity` INTEGER NOT NULL,
    `price` DECIMAL(10, 2) NOT NULL,

    INDEX `order_detail_order_id_idx`(`order_id`),
    INDEX `order_detail_laptop_model_id_idx`(`laptop_model_id`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `payment` (
    `id` BINARY(16) NOT NULL,
    `customer_id` BINARY(16) NOT NULL,
    `order_id` BINARY(16) NOT NULL,
    `type` ENUM('VNPay', 'Momo', 'ZaloPay', 'CashOnDelivery') NOT NULL,
    `status` ENUM('Pending', 'Success', 'Failed') NOT NULL,
    `payment_method_id` BINARY(16) NULL,

    UNIQUE INDEX `payment_order_id_key`(`order_id`),
    INDEX `payment_customer_id_idx`(`customer_id`),
    INDEX `payment_order_id_idx`(`order_id`),
    INDEX `payment_status_idx`(`status`),
    INDEX `payment_type_idx`(`type`),
    INDEX `payment_payment_method_id_idx`(`payment_method_id`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `payment_method` (
    `id` BINARY(16) NOT NULL,
    `data` JSON NOT NULL,
    `type` ENUM('VNPay', 'Momo', 'ZaloPay', 'CashOnDelivery') NOT NULL,

    INDEX `payment_method_type_idx`(`type`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `laptop_on_image` (
    `laptop_model_id` BINARY(16) NOT NULL,
    `image_id` BINARY(16) NOT NULL,

    PRIMARY KEY (`laptop_model_id`, `image_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `image` (
    `id` BINARY(16) NOT NULL,
    `image_url` VARCHAR(191) NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `comment` (
    `id` BINARY(16) NOT NULL,
    `laptop_model_id` BINARY(16) NOT NULL,
    `account_id` BINARY(16) NOT NULL,
    `parent_id` BINARY(16) NULL,
    `body` VARCHAR(191) NOT NULL,

    INDEX `comment_laptop_model_id_idx`(`laptop_model_id`),
    INDEX `comment_account_id_idx`(`account_id`),
    INDEX `comment_parent_id_idx`(`parent_id`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `chat` (
    `id` BINARY(16) NOT NULL,
    `sender_id` BINARY(16) NOT NULL,
    `receiver_id` BINARY(16) NOT NULL,
    `message` VARCHAR(191) NOT NULL,
    `create_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `address` (
    `id` BINARY(16) NOT NULL,
    `customer_id` BINARY(16) NOT NULL,
    `city` VARCHAR(191) NOT NULL,
    `district` VARCHAR(191) NOT NULL,
    `ward` VARCHAR(191) NULL,
    `street` VARCHAR(191) NOT NULL,
    `phone` VARCHAR(191) NOT NULL,

    INDEX `address_customer_id_idx`(`customer_id`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `cart` (
    `id` BINARY(16) NOT NULL,
    `customer_id` BINARY(16) NOT NULL,

    INDEX `cart_customer_id_idx`(`customer_id`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `laptop_on_cart` (
    `cart_id` BINARY(16) NOT NULL,
    `laptop_model_id` BINARY(16) NOT NULL,
    `quantity` INTEGER NOT NULL,

    PRIMARY KEY (`cart_id`, `laptop_model_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `customer` ADD CONSTRAINT `customer_account_id_fkey` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `admin` ADD CONSTRAINT `admin_account_id_fkey` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_sale` ADD CONSTRAINT `laptop_on_sale_laptop_model_id_fkey` FOREIGN KEY (`laptop_model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_sale` ADD CONSTRAINT `laptop_on_sale_sale_id_fkey` FOREIGN KEY (`sale_id`) REFERENCES `sale`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop` ADD CONSTRAINT `laptop_model_id_fkey` FOREIGN KEY (`model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `order` ADD CONSTRAINT `order_customer_id_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customer`(`account_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `order_detail` ADD CONSTRAINT `order_detail_order_id_fkey` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `order_detail` ADD CONSTRAINT `order_detail_laptop_model_id_fkey` FOREIGN KEY (`laptop_model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `payment` ADD CONSTRAINT `payment_order_id_fkey` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `payment` ADD CONSTRAINT `payment_customer_id_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customer`(`account_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `payment` ADD CONSTRAINT `payment_payment_method_id_fkey` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method`(`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_image` ADD CONSTRAINT `laptop_on_image_laptop_model_id_fkey` FOREIGN KEY (`laptop_model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_image` ADD CONSTRAINT `laptop_on_image_image_id_fkey` FOREIGN KEY (`image_id`) REFERENCES `image`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `comment` ADD CONSTRAINT `comment_laptop_model_id_fkey` FOREIGN KEY (`laptop_model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `comment` ADD CONSTRAINT `comment_account_id_fkey` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `comment` ADD CONSTRAINT `comment_parent_id_fkey` FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `chat` ADD CONSTRAINT `chat_sender_id_fkey` FOREIGN KEY (`sender_id`) REFERENCES `account`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `chat` ADD CONSTRAINT `chat_receiver_id_fkey` FOREIGN KEY (`receiver_id`) REFERENCES `account`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `address` ADD CONSTRAINT `address_customer_id_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customer`(`account_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `cart` ADD CONSTRAINT `cart_customer_id_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customer`(`account_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_cart` ADD CONSTRAINT `laptop_on_cart_cart_id_fkey` FOREIGN KEY (`cart_id`) REFERENCES `cart`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `laptop_on_cart` ADD CONSTRAINT `laptop_on_cart_laptop_model_id_fkey` FOREIGN KEY (`laptop_model_id`) REFERENCES `laptop_model`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

