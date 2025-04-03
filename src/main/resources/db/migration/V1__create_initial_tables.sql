-- Tabela de usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de mesas
CREATE TABLE tables (
    id BIGSERIAL PRIMARY KEY,
    number INT NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    current_order_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de itens do estoque
CREATE TABLE stock_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    unit VARCHAR(50) NOT NULL,
    min_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de itens do cardápio
CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    price DECIMAL(10, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(255) NULL,
    category VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de receitas (relação entre menu_items e stock_items)
CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    menu_item_id BIGINT NOT NULL,
    stock_item_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recipe_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
    CONSTRAINT fk_recipe_stock_item FOREIGN KEY (stock_item_id) REFERENCES stock_items(id),
    CONSTRAINT uk_recipe_item UNIQUE (menu_item_id, stock_item_id)
);

-- Tabela de pedidos
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    table_id BIGINT NULL,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    is_delivery BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES tables(id)
);

-- Adicionar referência em tables para orders
ALTER TABLE tables ADD CONSTRAINT fk_table_current_order FOREIGN KEY (current_order_id) REFERENCES orders(id);

-- Tabela de itens do pedido
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_time DECIMAL(10, 2) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_item_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Inserir um usuário admin inicial para testes
INSERT INTO users (name, email, password, role)
VALUES ('Admin', 'admin@wakari.com', '$2a$10$N/qXDNbGChVzVe4tqj1oiOEsS.D7cZTWJHJwpRZ4.aVgIBTBRZAaG', 'ADMIN');
-- Senha: admin123 