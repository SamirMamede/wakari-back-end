-- Adiciona a coluna capacity à tabela tables
ALTER TABLE tables ADD COLUMN capacity INT NOT NULL DEFAULT 4;

-- Remove o valor default após adicionar a coluna
ALTER TABLE tables ALTER COLUMN capacity DROP DEFAULT; 