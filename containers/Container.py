from dependency_injector import containers, providers
from services.actions.ActionsService import *
from tinydb import TinyDB
from persistence.Persistence import *
from services.connection.Serial import SerialService
from services.sensors.Sensors import SensorsService


class Container(containers.DeclarativeContainer):

    # config = providers.Configuration(ini_files=["config.ini"])
    config = providers.Configuration()

    # logging = providers.Resource(
    #    logging.config.fileConfig,
    #    fname="logging.ini",
    # )

    actionsService = providers.Singleton(
        ActionsService
    )

    sensorsService = providers.Singleton(
        SensorsService
    )

    tiny_db = providers.Singleton(
        TinyDB,
        'db.json'
    )

    # service = providers.Factory(
    #   Service,
    #   api_client=api_client,
    # )

    persistence = providers.Factory(
        Persistence,
        db=tiny_db,
    )

    serialService = providers.Factory(
        SerialService,
        actionsService,
        sensorsService
    )
