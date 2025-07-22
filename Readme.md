Init mongodb: <br/>
podman exec -it mongodb mongosh --eval "rs.initiate({
_id: \"rs0\",
members: [
{_id: 0, host: \"localhost\"}
]
})"
