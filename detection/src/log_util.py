import logging

log_agent = None

def init_logger(output_file: str = None) -> None:
    global log_agent
    log_agent = logging.getLogger('detection')
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
    logging.info('Logger initialized')

    if output_file:
        file_handler = logging.FileHandler(output_file)
        file_handler.setLevel(logging.INFO)
        file_handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
        logging.getLogger().addHandler(file_handler)
        logging.info(f'Logger output file: {output_file}')

def log(msg: str) -> None:
    if log_agent:
        log_agent.info(msg)
    else:
        raise Exception('Logger not initialized')


