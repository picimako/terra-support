//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

const wdioConf = require('./config/wdio/wdio.conf');

const config = {
    ...wdioConf.config,
};

exports.config = config;
