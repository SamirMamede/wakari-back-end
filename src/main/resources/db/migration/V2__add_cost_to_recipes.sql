-- Adiciona a coluna cost à tabela recipes
ALTER TABLE recipes ADD COLUMN cost DECIMAL(10, 2) NOT NULL DEFAULT 0;

-- Remove o valor default após adicionar a coluna
ALTER TABLE recipes ALTER COLUMN cost DROP DEFAULT; 