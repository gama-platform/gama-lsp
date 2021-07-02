# gama-lsp
[Work in progress] Language Server for Gama Language

## How to build

1. Run `mvn package`
2. Files are generated in `./gama.core.lang.ide/target/gama.core.lang.ide-1.0.0-SNAPSHOT-ls.jar`

## Please don't mass commit files

Before committing, please only add files that are really matter (files that you directly wrote), because there are a lot of generated files. To remove generated file, run `./prune.sh`, it will stash all the un-committed files and drop them all.

## Developing

Most dependencies will be detect automatically by Eclipse. But Eclipse will not autocomplete generated files. In order to get auto completion, run

```bash
mvn compile
```

and Maven will generate some Java files.

## Try it out

More editors TBA

### Neovim

First, install `lspconfig` package. Then, inside `.config/nvim` directory, create directory `lua/` and file `lua/gaml.lua`:

```lua
local lspconfig = require('lspconfig')
local configs = require('lspconfig.configs')

if "table" ~= type(lspconfig.gaml) then
    configs.gaml = {
        default_config = {
            name = 'gaml',
            cmd = {
                'java',
                '-cp',
                '/path/to/gama.core.lang.ide-1.0.0-SNAPSHOT-ls.jar', -- CHANGE THIS TO THE CORRECT PATH ON YOUR COMPUTER
                'gama.core.lang.RunLSP'
            },
            filetypes = {'gaml'},
            root_dir = function(fname)
                return lspconfig.util.find_git_ancestor(fname) or vim.loop.os_homedir()
            end,
            settings = {}
        }
    }
end

lspconfig.gaml.setup{ autostart = true }
```

Finally, include this at the end of `init.nvim`
```
augroup GAMLFILETYPE
  autocmd BufRead *.gaml setlocal filetype=gaml
augroup END
lua require("gaml")
```

## Current progress

- [x] Detects Symbol/Symbol Types
- [ ] Built in type, actions, facet
- [ ] Better completion (current one is shitty)
- [ ] Auto format
- [ ] CI/CD, I idealy when there are commit regarding the language in Gama repository, take related source code files and push them in this repo
