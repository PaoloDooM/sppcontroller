from dependency_injector import containers, providers
from services.actions.Executer import *
from controllers.ActionsTabController import *
from tinydb import TinyDB
from persistence.Persistence import *


class Container(containers.DeclarativeContainer):

    # config = providers.Configuration(ini_files=["config.ini"])
    config = providers.Configuration()

    # logging = providers.Resource(
    #    logging.config.fileConfig,
    #    fname="logging.ini",
    # )

    executer = providers.Singleton(
        Executer
    )

    actionsTabController = providers.Singleton(
        ActionsTabController
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
