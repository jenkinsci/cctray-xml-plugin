Behaviour.addLoadEvent(function() {
        var rssbar = document.getElementById('rss-bar')
        if (typeof rssbar !== 'undefined' && rssbar !== null) {
            rssbar.insertAdjacentHTML('beforeend', `
                <a href="cc.xml/" class="yui-button link-button">
                  <img src="${resURL}/plugin/cctray-xml/images/svgs/Icon-txt.svg"
                      class="leading-icon" height="16px"/> cc.xml
                </a>
                <a href="cc.xml/?recursive" class="yui-button link-button">
                  <img src="${resURL}/plugin/cctray-xml/images/svgs/Icon-txt.svg"
                      class="leading-icon" height="16px"/> recursive cc.xml
                </a>`);
        }
});
