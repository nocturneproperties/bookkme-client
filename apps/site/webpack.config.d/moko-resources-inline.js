;(function(config) {
    config.module.rules
        .filter(rule => rule.type === 'asset/resource' && Array.isArray(rule.resource))
        .filter(rule => rule.resource.some(resource => String(resource).endsWith('localization')))
        .forEach(rule => { rule.type = 'asset/inline'; });
})(config);
