function debug_log(message) {
    if (process.env.DEBUG)
        console.log(message);
}



module.exports = {
    debug_log
};
