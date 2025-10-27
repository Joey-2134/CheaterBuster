import {API_BASE_URL} from "../util/constants.js";

export const getPlayerCount = async () => {
    const response = await fetch(`${API_BASE_URL}/gathering/count`);
    if (!response.ok) throw new Error(`API error: ${response.status}`);
    return await response.json();
};

